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

import com.teragrep.jos_01.procfs.RowFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Provides information about memory usage, measured in pages.
// Some of these values are inaccurate because of a kernel-internal scalability optimization.
// If accurate values are required, use smaps or smaps_rollup instead, which are much slower but provide accurate, detailed information
public class Meminfo implements Status {

    private final Logger LOGGER = LoggerFactory.getLogger(Meminfo.class);
    private final ArrayList<String> rows;
    private final LocalDateTime timestamp;
    private final Map<String, String> statistics;

    private enum fields {
        MemTotal,
        MemFree,
        MemAvailable,
        Buffers,
        Cached,
        SwapCached,
        Active,
        Inactive,
        Activeanon,
        Inactiveanon,
        Activefile,
        Inactivefile,
        Unevictable,
        Mlocked,
        SwapTotal,
        SwapFree,
        Zswap,
        Zswapped,
        Dirty,
        Writeback,
        AnonPages,
        Mapped,
        Shmem,
        KReclaimable,
        Slab,
        SReclaimable,
        SUnreclaim,
        KernelStack,
        PageTables,
        SecPageTables,
        NFS_Unstable,
        Bounce,
        WritebackTmp,
        CommitLimit,
        Committed_AS,
        VmallocTotal,
        VmallocUsed,
        VmallocChunk,
        Percpu,
        HardwareCorrupted,
        AnonHugePages,
        ShmemHugePages,
        ShmemPmdMapped,
        FileHugePages,
        FilePmdMapped,
        CmaTotal,
        CmaFree,
        HugePages_Total,
        HugePages_Free,
        HugePages_Rsvd,
        HugePages_Surp,
        Hugepagesize,
        Hugetlb,
        DirectMap4k,
        DirectMap2M,
        DirectMap1G
    };

    public Meminfo(RowFile rowFile) throws IOException {
        this.rows = rowFile.readFile();
        statistics = new LinkedHashMap<String, String>();
        Pattern pattern = Pattern.compile("([a-zA-Z0-9_\\(\\)]*): *(\\d*)");
        for (String row : rows) {
            Matcher matcher = pattern.matcher(row);
            if (matcher.find()) {
                statistics.put(matcher.group(1), matcher.group(2));
            }
        }
        timestamp = LocalDateTime.now();
    }

    public Map<String, String> statistics() {
        return statistics;
    }

    public void printStatistics() {
        for (Map.Entry<String, String> statistic : statistics.entrySet()) {
            LOGGER.info(statistic.getKey() + ": ");
            LOGGER.info(statistic.getValue());
        }
    }

    public ArrayList<String> rows() {
        return this.rows;
    }

    public LocalDateTime timestamp() {
        return timestamp;
    }

    public void printTimestamp() {
        LOGGER.info(timestamp.format(DateTimeFormatter.ISO_DATE));
    }
}
