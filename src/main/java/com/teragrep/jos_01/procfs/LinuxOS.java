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

import java.io.File;
import java.util.ArrayList;

import com.teragrep.jos_01.procfs.status.Meminfo;
import com.teragrep.jos_01.procfs.status.OSStat;
import com.teragrep.jos_01.procfs.status.Vmstat;

public class LinuxOS {

    private final File procDirectory;

    public LinuxOS() {
        this("/proc");
    }
    public LinuxOS(String procDirectoryPath){
        procDirectory = new File(procDirectoryPath);
    }

    private ArrayList<String> readProcFile(String procFileName) {
        RowFile procFile = new RowFile(procDirectory, procFileName);
        ArrayList<String> rows = procFile.readFile();
        return rows;
    }

    public OSStat stat() {
        ArrayList<String> rows = readProcFile("stat");
        return new OSStat(rows);
    }

    public Vmstat vmstat(){
        ArrayList<String> rows = readProcFile("vmstat");
        return new Vmstat(rows);
    }

    public Meminfo meminfo(){
        ArrayList<String> rows = readProcFile("meminfo");
        return new Meminfo(rows);
    }

    // Estimates page size in kB.
    float pageSize() {
        Vmstat vmstat = vmstat();
        Meminfo meminfo = meminfo();
        float mapped = Long.parseLong(meminfo.statistics().get("Mapped"));
        float nr_mapped = Long.parseLong(vmstat.statistics().get("nr_mapped"));
        float pageSize = mapped / nr_mapped;
        return pageSize;
    }
    // Returns total RAM in kB
    public long totalRAM(){
        Meminfo meminfo = meminfo();
        return Long.parseLong(meminfo.statistics().get("MemTotal"));
    }

    public int cpuCount(){
        int cpuCount = 0;
        ArrayList<String> cpuinfo = proc("cpuinfo");
        for (String row : cpuinfo){
            if(row.startsWith("processor")){
                cpuCount++;
            }
        }
        return cpuCount;
    }

    public int cpuTicksPerSecond(){
        return 10;
    }


    public ArrayList<String> proc(String procFileName) {
        ArrayList<String> rows = readProcFile(procFileName);
        return rows;
    }
}
