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
package com.teragrep.jos_01.procfs.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Vmstat does not always have predefined number of fields on all systems.
// The fields listed by this object are the fields declared in https://man7.org/linux/man-pages/man5/procfs.5.html
public class Vmstat implements Status {

    private final ArrayList<String> rows;
    private final LocalDateTime timestamp;
    private final Map<String, String> statistics;
    private enum statFields {nr_free_pages,nr_alloc_batch,nr_inactive_anon,nr_active_anon,nr_inactive_file,nr_active_file,nr_unevictable,nr_mlock,nr_anon_pages,nr_mapped,nr_file_pages,nr_dirty,nr_writeback,nr_slab_reclaimable,nr_slab_unreclaimable,nr_page_table_pages,nr_kernel_stack,nr_unstable,nr_bounce,nr_vmscan_write,nr_vmscan_immediate_reclaim,nr_writeback_temp,nr_isolated_anon,nr_isolated_file,nr_shmem,nr_dirtied,nr_written,nr_pages_scanned,numa_hit,numa_miss,numa_foreign,numa_interleave,numa_local,numa_other,workingset_refault,workingset_activate,workingset_nodereclaim,nr_anon_transparent_hugepages,nr_free_cma,nr_dirty_threshold,nr_dirty_background_threshold,pgpgin,pgpgout,pswpin,pswpout,pgalloc_dma,pgalloc_dma32,pgalloc_normal,pgalloc_high,pgalloc_movable,pgfree,pgactivate,pgdeactivate,pgfault,pgmajfault,pgrefill_dma,pgrefill_dma32,pgrefill_normal,pgrefill_high,pgrefill_movable,pgsteal_kswapd_dma,pgsteal_kswapd_dma32,pgsteal_kswapd_normal,pgsteal_kswapd_high,pgsteal_kswapd_movable,pgsteal_direct_dma,pgsteal_direct_dma32,pgsteal_direct_normal,pgsteal_direct_high,pgsteal_direct_movable,pgscan_kswapd_dma,pgscan_kswapd_dma32,pgscan_kswapd_normal,pgscan_kswapd_high,pgscan_kswapd_movable,pgscan_direct_dma,pgscan_direct_dma32,pgscan_direct_normal,pgscan_direct_high,pgscan_direct_movable,pgscan_direct_throttle,zone_reclaim_failed,pginodesteal,slabs_scanned,kswapd_inodesteal,kswapd_low_wmark_hit_quickly,kswapd_high_wmark_hit_quickly,pageoutrun,allocstall,pgrotated,drop_pagecache,drop_slab,numa_pte_updates,numa_huge_pte_updates,numa_hint_faults,numa_hint_faults_local,numa_pages_migrated,pgmigrate_success,pgmigrate_fail,compact_migrate_scanned,compact_free_scanned,compact_isolated,compact_stall,compact_fail,compact_success,htlb_buddy_alloc_success,htlb_buddy_alloc_fail,unevictable_pgs_culled,unevictable_pgs_scanned,unevictable_pgs_rescued,unevictable_pgs_mlocked,unevictable_pgs_munlocked,unevictable_pgs_cleared,unevictable_pgs_stranded,thp_fault_alloc,thp_fault_fallback,thp_collapse_alloc,thp_collapse_alloc_failed,thp_split,thp_zero_page_alloc,thp_zero_page_alloc_failed,balloon_inflate,balloon_deflate,balloon_migrate,nr_tlb_remote_flush,nr_tlb_remote_flush_received,nr_tlb_local_flush_all,nr_tlb_local_flush_one,vmacache_find_calls,vmacache_find_hits,vmacache_full_flushes};

    public Vmstat(ArrayList<String> rows) {
        this.rows = rows;
        statistics = new LinkedHashMap<String, String>();
        for (String row : rows) {

            Pattern pattern = Pattern.compile("(.*) (.*)");
            Matcher matcher = pattern.matcher(row);
            if(matcher.find()){
                for(int i = 0; i < matcher.groupCount();i++){
                    statistics.put(matcher.group(1),matcher.group(2));
                }
            }
        }
        timestamp = LocalDateTime.now();
    }

    public Map<String, String> statistics() {
        return statistics;
    }

    public void printStatistics() {
        for (Map.Entry<String, String> statistic : statistics.entrySet()) {
            System.out.print(statistic.getKey() + ": ");
            System.out.println(statistic.getValue());
        }
    }

    public ArrayList<String> rows() {
        return this.rows;
    }

    public LocalDateTime timestamp() {
        return timestamp;
    }

    public void printTimestamp() {
        System.out.println(timestamp);
    }
}
