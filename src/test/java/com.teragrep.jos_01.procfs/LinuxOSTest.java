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

import com.teragrep.jos_01.procfs.status.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class LinuxOSTest {

    @Test
    public void upTimeTest() throws IOException {

        LinuxOS os = new LinuxOS();
        Uptime uptime = os.uptime();
        Assertions.assertEquals(2, uptime.statistics().size());
        Assertions.assertTrue(Float.parseFloat(uptime.statistics().get("uptimeSeconds")) > 0);
        Assertions.assertTrue(Float.parseFloat(uptime.statistics().get("combinedCpuCoreIdleTimeSeconds")) > 0);
    }

    @Test
    public void statTest() throws IOException {

        LinuxOS os = new LinuxOS();
        OSStat stat = os.stat();
        Assertions.assertEquals(12, stat.statistics().size());

        String[] expectedKeys = {
                "cpu",
                "cpu0",
                "cpu1",
                "cpu2",
                "cpu3",
                "intr",
                "ctxt",
                "btime",
                "processes",
                "procs_running",
                "procs_blocked",
                "softirq"
        };
        for (String key : expectedKeys) {
            Assertions.assertTrue(stat.statistics().containsKey(key));
        }
    }

    @Test
    public void cpuInfoTest() throws IOException {
        LinuxOS os = new LinuxOS();
        Cpuinfo cpuinfo = os.cpuinfo();
        Assertions.assertEquals(os.cpuThreadCount() * 27, cpuinfo.statistics().size());
        String[] expectedKeys = {
                "processor",
                "vendor_id",
                "cpu family",
                "model",
                "model name",
                "stepping",
                "microcode",
                "cpu MHz",
                "cache size",
                "physical id",
                "siblings",
                "core id",
                "cpu cores",
                "apicid",
                "initial apicid",
                "fpu",
                "fpu_exception",
                "cpuid level",
                "wp",
                "flags",
                "vmx flags",
                "bugs",
                "bogomips",
                "clflush size",
                "cache_alignment",
                "address sizes",
                "power management"
        };

        System.out.println(cpuinfo.statistics());
        for (int i = 0; i < cpuinfo.cpuThreadCount(); i++) {
            for (String key : expectedKeys) {
                Assertions.assertTrue(cpuinfo.statistics().containsKey(key + "_" + i));
            }
        }
    }

    @Test
    public void memInfoTest() throws IOException {
        LinuxOS os = new LinuxOS();
        Meminfo meminfo = os.meminfo();
        Assertions.assertEquals(56, meminfo.statistics().size());

        String[] expectedKeys = {
                "MemTotal",
                "MemFree",
                "MemAvailable",
                "Buffers",
                "Cached",
                "SwapCached",
                "Active",
                "Inactive",
                "Active(anon)",
                "Inactive(anon)",
                "Active(file)",
                "Inactive(file)",
                "Unevictable",
                "Mlocked",
                "SwapTotal",
                "SwapFree",
                "Zswap",
                "Zswapped",
                "Dirty",
                "Writeback",
                "AnonPages",
                "Mapped",
                "Shmem",
                "KReclaimable",
                "Slab",
                "SReclaimable",
                "SUnreclaim",
                "KernelStack",
                "PageTables",
                "SecPageTables",
                "NFS_Unstable",
                "Bounce",
                "WritebackTmp",
                "CommitLimit",
                "Committed_AS",
                "VmallocTotal",
                "VmallocUsed",
                "VmallocChunk",
                "Percpu",
                "HardwareCorrupted",
                "AnonHugePages",
                "ShmemHugePages",
                "ShmemPmdMapped",
                "FileHugePages",
                "FilePmdMapped",
                "CmaTotal",
                "CmaFree",
                "HugePages_Total",
                "HugePages_Free",
                "HugePages_Rsvd",
                "HugePages_Surp",
                "Hugepagesize",
                "Hugetlb",
                "DirectMap4k",
                "DirectMap2M",
                "DirectMap1G"
        };
        System.out.println(meminfo.statistics());
        for (String key : expectedKeys) {
            Assertions.assertTrue(meminfo.statistics().containsKey(key));
        }
    }

    @Test
    public void highLevelMethodsTest() throws IOException {
        LinuxOS os = new LinuxOS();
        Assertions.assertEquals(1, os.cpuCount());
        Assertions.assertEquals(4, os.cpuThreadCount());
        Assertions.assertEquals(4, os.cpuPhysicalCoreCount());
        Assertions.assertEquals(4.0, os.pageSize(), 0);
        Assertions.assertEquals(32790864, os.totalRAM());
    }
}
