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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class LinuxOSTest {

    private final Logger LOGGER = LoggerFactory.getLogger(LinuxOSTest.class);

    // Uptime should have 2 fields, and the values should be positive floats.
    @Test
    public void upTimeTest() throws IOException {
        LinuxOS os = new LinuxOS();
        Uptime uptime = os.uptime();
        Assertions.assertEquals(2, uptime.statistics().size());
        Assertions.assertTrue(Float.parseFloat(uptime.statistics().get("uptimeSeconds")) > 0);
        Assertions.assertTrue(Float.parseFloat(uptime.statistics().get("combinedCpuCoreIdleTimeSeconds")) > 0);
    }

    // Stat should have 7 fields in addition to cpu lines, and all expected keys should be present.
    // The number of cpu lines is one aggregate line + one line for each logical core. The number of logical cores should be the same as reported in /proc/cpuinfo
    @Test
    public void statTest() throws IOException {

        LinuxOS os = new LinuxOS();
        OSStat stat = os.stat();

        int numberOfCpus = 0;
        for (Map.Entry entry : stat.statistics().entrySet()) {
            if(entry.getKey().toString().startsWith("cpu")){
                numberOfCpus++;
            }
        }
        Assertions.assertEquals(7+numberOfCpus, stat.statistics().size());

        String[] expectedKeys = {
                "cpu",
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

    // Check that vmStat has all expected keys, and does not contain any unexpected keys.
    // VMStat should have 187 fields
    @Test
    public void vmStatTest() throws IOException {
        LinuxOS os = new LinuxOS();
        Vmstat vmstat = os.vmstat();

        Assertions.assertEquals(178, vmstat.statistics().size());
        String[] expectedKeys = {
                "nr_free_pages",
                "nr_zone_inactive_anon",
                "nr_zone_active_anon",
                "nr_zone_inactive_file",
                "nr_zone_active_file",
                "nr_zone_unevictable",
                "nr_zone_write_pending",
                "nr_mlock",
                "nr_bounce",
                "nr_zspages",
                "nr_free_cma",
                "numa_hit",
                "numa_miss",
                "numa_foreign",
                "numa_interleave",
                "numa_local",
                "numa_other",
                "nr_inactive_anon",
                "nr_active_anon",
                "nr_inactive_file",
                "nr_active_file",
                "nr_unevictable",
                "nr_slab_reclaimable",
                "nr_slab_unreclaimable",
                "nr_isolated_anon",
                "nr_isolated_file",
                "workingset_nodes",
                "workingset_refault_anon",
                "workingset_refault_file",
                "workingset_activate_anon",
                "workingset_activate_file",
                "workingset_restore_anon",
                "workingset_restore_file",
                "workingset_nodereclaim",
                "nr_anon_pages",
                "nr_mapped",
                "nr_file_pages",
                "nr_dirty",
                "nr_writeback",
                "nr_writeback_temp",
                "nr_shmem",
                "nr_shmem_hugepages",
                "nr_shmem_pmdmapped",
                "nr_file_hugepages",
                "nr_file_pmdmapped",
                "nr_anon_transparent_hugepages",
                "nr_vmscan_write",
                "nr_vmscan_immediate_reclaim",
                "nr_dirtied",
                "nr_written",
                "nr_throttled_written",
                "nr_kernel_misc_reclaimable",
                "nr_foll_pin_acquired",
                "nr_foll_pin_released",
                "nr_kernel_stack",
                "nr_page_table_pages",
                "nr_sec_page_table_pages",
                "nr_swapcached",
                "pgpromote_success",
                "pgpromote_candidate",
                "nr_dirty_threshold",
                "nr_dirty_background_threshold",
                "pgpgin",
                "pgpgout",
                "pswpin",
                "pswpout",
                "pgalloc_dma",
                "pgalloc_dma32",
                "pgalloc_normal",
                "pgalloc_movable",
                "pgalloc_device",
                "allocstall_dma",
                "allocstall_dma32",
                "allocstall_normal",
                "allocstall_movable",
                "allocstall_device",
                "pgskip_dma",
                "pgskip_dma32",
                "pgskip_normal",
                "pgskip_movable",
                "pgskip_device",
                "pgfree",
                "pgactivate",
                "pgdeactivate",
                "pglazyfree",
                "pgfault",
                "pgmajfault",
                "pglazyfreed",
                "pgrefill",
                "pgreuse",
                "pgsteal_kswapd",
                "pgsteal_direct",
                "pgsteal_khugepaged",
                "pgdemote_kswapd",
                "pgdemote_direct",
                "pgdemote_khugepaged",
                "pgscan_kswapd",
                "pgscan_direct",
                "pgscan_khugepaged",
                "pgscan_direct_throttle",
                "pgscan_anon",
                "pgscan_file",
                "pgsteal_anon",
                "pgsteal_file",
                "zone_reclaim_failed",
                "pginodesteal",
                "slabs_scanned",
                "kswapd_inodesteal",
                "kswapd_low_wmark_hit_quickly",
                "kswapd_high_wmark_hit_quickly",
                "pageoutrun",
                "pgrotated",
                "drop_pagecache",
                "drop_slab",
                "oom_kill",
                "numa_pte_updates",
                "numa_huge_pte_updates",
                "numa_hint_faults",
                "numa_hint_faults_local",
                "numa_pages_migrated",
                "pgmigrate_success",
                "pgmigrate_fail",
                "thp_migration_success",
                "thp_migration_fail",
                "thp_migration_split",
                "compact_migrate_scanned",
                "compact_free_scanned",
                "compact_isolated",
                "compact_stall",
                "compact_fail",
                "compact_success",
                "compact_daemon_wake",
                "compact_daemon_migrate_scanned",
                "compact_daemon_free_scanned",
                "htlb_buddy_alloc_success",
                "htlb_buddy_alloc_fail",
                "cma_alloc_success",
                "cma_alloc_fail",
                "unevictable_pgs_culled",
                "unevictable_pgs_scanned",
                "unevictable_pgs_rescued",
                "unevictable_pgs_mlocked",
                "unevictable_pgs_munlocked",
                "unevictable_pgs_cleared",
                "unevictable_pgs_stranded",
                "thp_fault_alloc",
                "thp_fault_fallback",
                "thp_fault_fallback_charge",
                "thp_collapse_alloc",
                "thp_collapse_alloc_failed",
                "thp_file_alloc",
                "thp_file_fallback",
                "thp_file_fallback_charge",
                "thp_file_mapped",
                "thp_split_page",
                "thp_split_page_failed",
                "thp_deferred_split_page",
                "thp_split_pmd",
                "thp_scan_exceed_none_pte",
                "thp_scan_exceed_swap_pte",
                "thp_scan_exceed_share_pte",
                "thp_split_pud",
                "thp_zero_page_alloc",
                "thp_zero_page_alloc_failed",
                "thp_swpout",
                "thp_swpout_fallback",
                "balloon_inflate",
                "balloon_deflate",
                "balloon_migrate",
                "swap_ra",
                "swap_ra_hit",
                "ksm_swpin_copy",
                "cow_ksm",
                "zswpin",
                "zswpout",
                "direct_map_level2_splits",
                "direct_map_level3_splits",
                "nr_unstable"
        };
        for (String key : expectedKeys) {
            Assertions.assertTrue(vmstat.statistics().containsKey(key));
        }

        for (Map.Entry entry : vmstat.statistics().entrySet()) {
            boolean isExpected = false;
            for (String key : expectedKeys) {
                if (key.equals(entry.getKey())) {
                    isExpected = true;
                    break;
                }
            }
            Assertions.assertTrue(isExpected);
        }
    }

    // CPUInfo should have 27 fields for every logical cpu (cpuThread) listed. Fields should be appended with an underscore and an index number
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
        for (int i = 0; i < cpuinfo.cpuThreadCount(); i++) {
            for (String key : expectedKeys) {
                Assertions.assertTrue(cpuinfo.statistics().containsKey(key + "_" + i));
            }
        }
    }

    // MemInfo should have exactly 56 fields and should contain all the expected keys.
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
        for (String key : expectedKeys) {
            Assertions.assertTrue(meminfo.statistics().containsKey(key));
        }
    }

    // Check that cpuCount and physicalCoreCount returns a positive integer
    // PageSize and totalRAM should return a positive float
    // TotalRAM should never exceed memory allocated to JVM
    // CpuThreadCount matches Runtime.availableProcessors()
    // CpuThreadCount should either match or be greater than the number of physical cores
    // CpuThreadCount and number of physical cores should be greater or equal to the number of CPUs installed
    @Test
    public void highLevelMethodsTest() throws IOException {
        LinuxOS os = new LinuxOS();
        Assertions.assertTrue(Integer.signum(os.cpuCount()) == 1);
        Assertions.assertEquals(Runtime.getRuntime().availableProcessors(), os.cpuThreadCount());
        Assertions.assertTrue(Integer.signum(os.cpuPhysicalCoreCount()) == 1);
        Assertions.assertTrue(os.cpuThreadCount() >= os.cpuPhysicalCoreCount());
        Assertions.assertTrue(os.cpuThreadCount() >= os.cpuCount() && os.cpuPhysicalCoreCount() >= os.cpuCount());
        Assertions.assertTrue(Integer.signum(os.cpuCount()) == 1);
        Assertions.assertTrue(os.pageSize() > 0);
        Assertions.assertTrue(os.totalRAM() > Runtime.getRuntime().maxMemory() / 1000);
    }
}
