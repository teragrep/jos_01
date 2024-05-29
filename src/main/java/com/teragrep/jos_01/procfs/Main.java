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

import java.util.ArrayList;
import com.teragrep.jos_01.procfs.status.ProcessStat;
import com.teragrep.jos_01.procfs.status.Statm;

public class Main {

    public static void main(String[] args) {
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
        System.out.println("Process stat = "+pstat.statistics());
        Statm statm = process.statm();
        System.out.println("Process statm = "+statm.statistics());

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
        System.out.println(process.residentSetSize());
        System.out.println(process.cpuTime());
        System.out.println(process.cpuUsage());

        // OS statistics are available via the OS class
        System.out.println("\nOS statistics are available via the OS class using similar methods");
        LinuxOS os = new LinuxOS();
        System.out.println(os.stat().statistics());
        System.out.println(os.vmstat().statistics());
        System.out.println(os.meminfo().statistics());
        System.out.println(os.cpuinfo().statistics());


        // OS also has high-level methods just like processes:
        System.out.println("\nOS also has high-level methods just like processes:");
        System.out.println("Number of physical CPUs: "+os.cpuCount());
        System.out.println("Number of physical CPU cores: "+os.cpuPhysicalCoreCount());
        System.out.println("Number of CPU threads (physical cores can have multiple threads): "+os.cpuThreadCount());
        System.out.println("OS CPU tick rate: "+ os.cpuTicksPerSecond());
    }
}
