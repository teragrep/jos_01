package com.teragrep.jos_01.procfs;

    final class FakeSysconf implements SysconfInterface {

        private final int tickRate;

        public FakeSysconf() {
            this(100);
        }

        public FakeSysconf(int tickRate) {
            this.tickRate = tickRate;
        }

        @Override
        public long main() {
            return tickRate;
        }
    }
