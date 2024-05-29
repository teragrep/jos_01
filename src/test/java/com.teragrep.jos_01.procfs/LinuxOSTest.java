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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LinuxOSTest {
    private LinuxOS os;
    @Before
    public void setUp(){
        os = new LinuxOS();
    }

    @Test
    public void upTimeTest() {
        Uptime uptime = os.uptime();
        Assert.assertEquals(2,uptime.statistics().size());
        Assert.assertTrue(Float.parseFloat(uptime.statistics().get("uptimeSeconds")) > 0);
        Assert.assertTrue(Float.parseFloat(uptime.statistics().get("combinedCpuCoreIdleTimeSeconds")) > 0);
    }

    @Test
    public void statTest() {
        OSStat stat = os.stat();
        Assert.assertEquals(12,stat.statistics().size());

        String[] expectedKeys = {"cpu", "cpu0", "cpu1", "cpu2", "cpu3", "intr", "ctxt", "btime", "processes", "procs_running", "procs_blocked", "softirq"};
        for(String key : expectedKeys){
            os.stat().statistics().containsKey(key);
        }
    }

    @Test
    public void cpuInfoTest() {
        Cpuinfo cpuinfo = os.cpuinfo();
        Assert.assertEquals(os.cpuThreadCount()*27,cpuinfo.statistics().size());
        String[] expectedKeys = {"processor","vendor_id","cpu family","model","model name","stepping","microcode","cpu MHz","cache size","physical id","siblings","core id","cpu cores","apicid","initial apicid","fpu","fpu_exception","cpuid level","wp","flags","vmx flags","bugs","bogomips","clflush size","cache_alignment","address sizes","power management"};

        System.out.println(cpuinfo.statistics());
        for(int i = 0; i < cpuinfo.cpuThreadCount();i++){
            for (String key : expectedKeys){
                Assert.assertTrue(cpuinfo.statistics().containsKey(key+"_"+i));
            }
        }
    }

    @Test
    public void memInfoTest() {
        Meminfo meminfo = os.meminfo();
        Assert.assertEquals(53,meminfo.statistics().size());

        String[] expectedKeys = {"MemTotal","MemFree","MemAvailable","Buffers","Cached","SwapCached","Active","Inactive","Active(anon)","Inactive(anon)","Active(file)","Inactive(file)","Unevictable","Mlocked","SwapTotal","SwapFree","Zswap","Zswapped","Dirty","Writeback","AnonPages","Mapped","Shmem","KReclaimable","Slab","SReclaimable","SUnreclaim","KernelStack","PageTables","SecPageTables","NFS_Unstable","Bounce","WritebackTmp","CommitLimit","Committed_AS","VmallocTotal","VmallocUsed","VmallocChunk","Percpu","HardwareCorrupted","AnonHugePages","ShmemHugePages","ShmemPmdMapped","FileHugePages","FilePmdMapped","CmaTotal","CmaFree","HugePages_Total","HugePages_Free","HugePages_Rsvd","HugePages_Surp","Hugepagesize","Hugetlb","DirectMap4k","DirectMap2M","DirectMap1G"};
        for(String key : expectedKeys){
            os.stat().statistics().containsKey(key);
        }
    }

    @Test
    public void highLevelMethodsTest() {
        Assert.assertEquals(1,os.cpuCount());
        Assert.assertEquals(4,os.cpuThreadCount());
        Assert.assertEquals(4,os.cpuPhysicalCoreCount());
        Assert.assertEquals(4.0,os.pageSize(),0);
        Assert.assertEquals(32790864,os.totalRAM());
    }
    }
