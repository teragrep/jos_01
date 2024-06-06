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
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Map;

public class ProcessTest {

    private final Logger LOGGER = LoggerFactory.getLogger(ProcessTest.class);
    Process systemd;
    Process kthreadd;

    public class TestThread implements Runnable {

        ProcessBuilder processBuilder;
        java.lang.Process process;

        public TestThread(ProcessBuilder processBuilder)
                throws IOException, NoSuchFieldException, IllegalAccessException {
            this.processBuilder = processBuilder;
        }

        @Override
        public void run() {
            try {
                this.process = processBuilder.start();
            }
            catch (IOException e) {
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
        while ((line = br.readLine()) != null) {
            if (line.contains(" " + command)) {
                pid = Integer.parseInt(line.trim().replaceAll("(\\d+).+", "$1"));
                pids.add(pid);
            }
        }
        return pids;
    }

    // Kthreadd process (pid = 1) should have 52 fields in its Stat file, and it should have (systemd) as comm value and 1 as pid value in the file. None of the fields should be null
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

    // Kthreadd process (pid = 2) should have 52 fields in its Stat file, and it should have (kthreadd) as comm value and 2 as pid value in the file. None of the fields should be null
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

    // Process object should be able to be instantiated with an integer and a String
    @Test
    public void constructorTest() {
        Process process = new Process(1);
        Process processString = new Process("1");
        Assertions.assertTrue(process.isAlive());
        Assertions.assertTrue(processString.isAlive());
    }

    // If a process with the designated ID is not found, the returned Process object should be a stub, and isAlive should return false.
    @Test
    public void noSuchProcessTest() {
        Process process = new Process(-1);
        Assertions.assertFalse(process.isAlive());
    }

    // After starting a sleeping process in a new thread, isAlive should return true. After killing the process, it should return false.
    @Test
    public void isAliveTest() throws IOException, NoSuchFieldException, IllegalAccessException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("sleep", "10");

        TestThread processThread = new TestThread(pb);
        processThread.run();

        ArrayList<Integer> pids = getPidByCommand("sleep");

        Process sleepingProcess = new Process(pids.get(0));

        Assertions.assertTrue(sleepingProcess.isAlive());

        java.lang.Process kill = Runtime.getRuntime().exec("kill " + pids.get(0));
        try {
            kill.waitFor();
        }
        catch (InterruptedException inter) {
            LOGGER.warn("Kill interrupted!");
        }
        Thread.sleep(10);
        Assertions.assertFalse(sleepingProcess.isAlive());

    }

    // Check that at least the top level of proc files defined here are present when asking for a list of file names from Systemd
    @Test
    public void procFileNamesTest() {
        Process process = new Process(1);
        ArrayList<String> fileNames = process.availableProcFiles();
        String[] expectedKeys = {
                "arch_status",
                "environ",
                "maps",
                "patch_state",
                "statm",
                "exe",
                "mem",
                "personality",
                "status",
                "autogroup",
                "mountinfo",
                "projid_map",
                "syscall",
                "auxv",
                "mounts",
                "root",
                "cgroup",
                "gid_map",
                "mountstats",
                "sched",
                "timens_offsets",
                "clear_refs",
                "io",
                "schedstat",
                "timers",
                "cmdline",
                "ksm_merging_pages",
                "sessionid",
                "timerslack_ns",
                "comm",
                "ksm_stat",
                "numa_maps",
                "setgroups",
                "uid_map",
                "coredump_filter",
                "latency",
                "oom_adj",
                "smaps",
                "wchan",
                "cpu_resctrl_groups",
                "limits",
                "oom_score",
                "smaps_rollup",
                "cpuset",
                "loginuid",
                "oom_score_adj",
                "stack",
                "cwd",
                "pagemap",
                "stat"
        };
        for (String key : expectedKeys) {
            boolean found = false;
            for (String fileName : fileNames) {
                if (("/" + key).equals(fileName)) {
                    found = true;
                    break;
                }
            }
            Assertions.assertTrue(found);
        }
    }

    // The number of tasks should be equal to the number of threads in /proc/pid/stat. Test for both systemd and the current JVM
    @Test
    public void correctNumberOfTasksTest() throws IOException {
        Process process = new Process(1);
        String statTaskcount = process.stat().statistics().get("num_threads");
        int taskCount = process.tasks().size();
        Assertions.assertEquals(statTaskcount, String.valueOf(taskCount));

        Process jvm = new Process(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        String jvmStatTaskcount = jvm.stat().statistics().get("num_threads");
        int jvmTaskCount = jvm.tasks().size();
        Assertions.assertEquals(jvmStatTaskcount, String.valueOf(jvmTaskCount));
    }

    // Stat status object should contain all of the listed fields.
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
            Assertions.assertNotNull(stat.statistics().get(key));
        }
    }

    // Statm status object should contain all of the listed fields.
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

    // Timestamps should be available and should be different if the proc method is called later
    @Test
    public void timestampTest() throws IOException {
        systemd = new Process(1);
        ProcessStat stat = systemd.stat();
        ProcessStat stat2 = systemd.stat();
        Statm statm = systemd.statm();

        // Timestamps should always have a value
        Assertions.assertTrue(stat.timestamp() != null);
        Assertions.assertTrue(statm.timestamp() != null);

        // Timestamps should be different if called at different times, even when calling the same method again
        Assertions.assertFalse(stat.timestamp().equals(stat2.timestamp()));
        Assertions.assertFalse(stat.timestamp().equals(statm.timestamp()));
    }

    // Get the JVM process this test is running in, and make two delayed calls to cpuTime to make sure cpuTime actually increments as process is in use
    // Uses SysconfInterface.Fake() to get a hardcoded CPU tick rate (100) without actually having to compile Native C code.
    @Test
    public void CpuTimeTest() throws IOException, InterruptedException {

        long pid = Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        Process jvm = new Process(pid);
        float cpuTime1 = jvm.cpuTime(new SysconfInterface.Fake());
        Thread.sleep(1500);
        float cpuTime2 = jvm.cpuTime(new SysconfInterface.Fake());
        Assertions.assertNotEquals(cpuTime2, cpuTime1);
    }

    // As resident set size for the whole JVM fluctuates a lot we need to create a real chonker of an object to be sure that RSS increases when creating objects
    @Test
    public void residentSetSizeTest() throws IOException, InterruptedException {
        Process jvm = new Process(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        float rssBefore = jvm.residentSetSize();
        float freeMemoryBefore = Runtime.getRuntime().freeMemory();
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < 10000000; i++) {
            numbers.add((int) Math.floor(Math.random() * 100));
        }
        float rssAfter = jvm.residentSetSize();
        float freeMemoryAfter = Runtime.getRuntime().freeMemory();
        Assertions.assertEquals(freeMemoryBefore, freeMemoryAfter + (rssAfter - rssBefore) * 1000, 20000000); // Based on testing JVM seems to allocate 15-20 megabytes of memory outside of the numbers object.
        numbers.clear();
    }

    // CpuUsage of JVM should increase over time
    @Test
    public void cpuUsageTest() throws IOException, InterruptedException {

        long pid = Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        Process jvm = new Process(pid);
        float cpuUsage1 = jvm.cpuUsage(new SysconfInterface.Fake());
        Thread.sleep(1500);
        float cpuUsage2 = jvm.cpuUsage(new SysconfInterface.Fake());
        Assertions.assertNotEquals(cpuUsage1, cpuUsage2);
    }
}
