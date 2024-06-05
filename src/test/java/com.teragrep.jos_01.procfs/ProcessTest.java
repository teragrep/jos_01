/*
 * Java Operating System Statistics JOS-01
 * Copyright (C) 2021-2024 Suomen Kanuuna Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Suomen Kanuuna Oy without any additional
 * modifications.
 *
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *
 * Origin of the software must be attributed to Suomen Kanuuna Oy. Any modified
 * versions must be marked as "Modified version of" The Program.
 *
 * Names of the licensors and authors may not be used for publicity purposes.
 *
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *
 * To the extent this program is licensed as part of the Commercial versions of
 * Teragrep, the applicable Commercial License may apply to this file if you as
 * a licensee so wish it.
 */
package com.teragrep.jos_01.procfs;

import com.teragrep.jos_01.procfs.status.ProcessStat;
import com.teragrep.jos_01.procfs.status.Statm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class ProcessTest {

    private final Logger LOGGER = LoggerFactory.getLogger(ProcessTest.class);
    Process systemd;
    Process kthreadd;
    public class TestThread implements Runnable {
        ProcessBuilder processBuilder;
        java.lang.Process process;
        long pid;
        public TestThread(ProcessBuilder processBuilder) throws IOException, NoSuchFieldException, IllegalAccessException {
            this.processBuilder = processBuilder;
        }
        @Override
        public void run() {
            try {
                this.process = processBuilder.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ArrayList<Integer> getPidByCommand(String command) throws IOException {
        ArrayList<Integer> pids = new ArrayList<Integer>();

        java.lang.Process ps = Runtime.getRuntime().exec("ps");
        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        String line;
        int pid = -1;
        while ((line = br.readLine()) != null){
            if(line.contains(command)){
                pid = Integer.parseInt(line.trim().replaceAll("(\\d+).+","$1"));
                pids.add(pid);
            }
        }
        return pids;
    }

    @Test
    public void readSystemdFileTest() throws IOException {
        systemd = new Process(1);
        Map<String, String> systemdStats = systemd.stat().statistics();
        Assertions.assertEquals("1", systemdStats.get("pid"));
        Assertions.assertEquals("(systemd)", systemdStats.get("comm"));
        Assertions.assertEquals(52, systemdStats.size());
        for (Map.Entry<String, String> entry : systemdStats.entrySet()) {
            Assertions.assertNotNull(entry.getValue());
            Assertions.assertNotNull(entry.getKey());
        }
    }

    @Test
    public void readKthreaddFileTest() throws IOException {
        kthreadd = new Process(2);
        Map<String, String> kthreaddStats = kthreadd.stat().statistics();
        Assertions.assertEquals("2", kthreaddStats.get("pid"));
        Assertions.assertEquals("(kthreadd)", kthreaddStats.get("comm"));
        Assertions.assertEquals(52, kthreaddStats.size());
        for (Map.Entry<String, String> entry : kthreaddStats.entrySet()) {
            Assertions.assertNotNull(entry.getValue());
            Assertions.assertNotNull(entry.getKey());
        }
    }

    @Test
    public void constructorTest() {
        Process process = new Process(1);
        Process processString = new Process("1");
        Assertions.assertTrue(process.isAlive());
        Assertions.assertTrue(processString.isAlive());
    }

    @Test
    public void noSuchProcessTest() {
        Process process = new Process(-1);
        Assertions.assertFalse(process.isAlive());
    }

    @Test
    public void isAliveTest() throws IOException, NoSuchFieldException, IllegalAccessException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("sleep","10");

        TestThread processThread = new TestThread(pb);
        processThread.run();

        ArrayList<Integer> pids = getPidByCommand("sleep");

        Process sleepingProcess = new Process(pids.get(0));

        Assertions.assertTrue(sleepingProcess.isAlive());

        java.lang.Process kill = Runtime.getRuntime().exec("kill "+ pids.get(0));
        try{
            kill.waitFor();
        }catch (InterruptedException inter){
            LOGGER.warn("Kill interrupted!");
        }

        Assertions.assertFalse(sleepingProcess.isAlive());

    }

    @Test
    public void procFileNamesTest() {
        Process process = new Process(1);
        ArrayList<String> fileNames = process.availableProcFiles();
        //Assertions.assertEquals(243, fileNames.size());  // This can change over time, find some other solution
    }

    @Test
    public void correctNumberOfTasksTest() throws IOException {
        Process process = new Process(1);
        String statTaskcount = process.stat().statistics().get("num_threads");
        int taskCount = process.tasks().size();
        Assertions.assertEquals(statTaskcount, String.valueOf(taskCount));
    }

    @Test
    public void statTest() throws IOException {
        String[] expectedKeys = {
                "pid",
                "comm",
                "state",
                "ppid",
                "pgrp",
                "session",
                "tty_nr",
                "tpgid",
                "flags",
                "minflt",
                "cminflt",
                "majflt",
                "cmajflt",
                "utime",
                "stime",
                "cutime",
                "cstime",
                "priority",
                "nice",
                "num_threads",
                "itrealvalue",
                "starttime",
                "vsize",
                "rss",
                "rsslim",
                "startcode",
                "endcode",
                "startstack",
                "kstkesp",
                "kstkeip",
                "signal",
                "blocked",
                "sigignore",
                "sigcatch",
                "wchan",
                "nswap",
                "cnswap",
                "exit_signal",
                "processor",
                "rt_priority",
                "policy",
                "delayacct_blkio_ticks",
                "guest_time",
                "cguest_time",
                "start_data",
                "end_data",
                "start_brk",
                "arg_start",
                "arg_end",
                "env_start",
                "env_end",
                "exit_code"
        };
        Process process = new Process(1);
        ProcessStat stat = process.stat();
        for (String key : expectedKeys) {
            LOGGER.debug("{}‚{}", key, stat.statistics().get(key));
            Assertions.assertNotNull(stat.statistics().get(key));
        }
    }

    @Test
    public void statmTest() throws IOException {
        String[] expectedKeys = {
                "size", "resident", "shared", "text", "lib", "data", "dt"
        };
        Process process = new Process(1);
        Statm statm = process.statm();
        for (String key : expectedKeys) {
            Assertions.assertNotNull(statm.statistics().get(key));
        }
    }
    /*
    Process process = new Process(1);
    // Find out if a process is alive with a simple call to isAlive().
        System.out.println("Find out if a process is alive with a simple call to isAlive().");
        System.out.println(process.isAlive());

    // Process methods for specific proc files will provide a status Object representing a snapshot of the proc file at the time of the call.
        System.out
                .println(
                "\nProcess methods for specific proc files will provide a status Object representing a snapshot of the proc file at the time of the call."
                );
    ProcessStat pstat = process.stat();
        System.out.println("Process stat = " + pstat.statistics());
    Statm statm = process.statm();
        System.out.println("Process statm = " + statm.statistics());

    // Status object contains a timestamp and a Map containing keys to find wanted field more easily. Specific proc files are also formatted properly
        System.out
                .println(
                "\nStatus object contains a timestamp and a Map containing keys to find wanted field more easily."
                );
        System.out.println(pstat.statistics());
        System.out.println(pstat.timestamp());

    // Processes can list all of its currently running Threads:
        System.out.println("\nProcesses can list all of its currently running Threads:");
    ArrayList<Task> tasks = process.tasks();
        System.out.println(tasks);

    // Each Thread has the ability to report on its status just like a Process can:
        System.out.println("\nEach Thread has the ability to report on its status just like a Process can:");
        System.out.println(tasks.get(0).stat().statistics());
        System.out.println(tasks.get(0).statm().statistics());

    // High level methods can be used to quickly calculate specific performance statistics:
        System.out.println("\nHigh level methods can be used to quickly calculate specific performance statistics:");
        System.out.println("RSS: " + process.residentSetSize());
        try {
        System.out.println("CpuTime: " + process.cpuTime());
        System.out.println("Cpu%: " + process.cpuUsage());
    }
        catch (IOException e) {
        System.err.println("Could not calculate Cpu statistics:\n" + e);
    }

    Sysconf sysconf = new Sysconf();
    long tickrate;
        try {
        tickrate = sysconf.main();
    }
        catch (IOException e) {
        System.err.println(e);
        System.out.println("Failed to get system clock tick rate! Defaulting to 100");
        tickrate = 100;
    }
        System.out.println("System tickrate: " + tickrate);

    // OS statistics are available via the OS class
        System.out.println("\nOS statistics are available via the OS class using similar methods");
    LinuxOS os = new LinuxOS();
        System.out.println(os.stat().statistics());
        System.out.println(os.vmstat().statistics());
        System.out.println(os.meminfo().statistics());
        System.out.println(os.cpuinfo().statistics());

    // OS also has high-level methods just like processes:
        System.out.println("\nOS also has high-level methods just like processes:");
        System.out.println("Number of physical CPUs: " + os.cpuCount());
        System.out.println("Number of physical CPU cores: " + os.cpuPhysicalCoreCount());
        System.out.println("Number of CPU threads (physical cores can have multiple threads): " + os.cpuThreadCount());
        try {
        System.out.println("OS CPU tick rate: " + os.cpuTicksPerSecond());
    }
        catch (IOException e) {
        System.out.println("Failed to get OS tick rate!");
    }

     */
}
