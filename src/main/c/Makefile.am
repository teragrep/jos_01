pkglib_LTLIBRARIES = sysconf.la

sysconf_la_SOURCES = sysconf.c
sysconf_la_CPPFLAGS = -I$(top_srcdir) $(PTHREADS_CFLAGS) $(RSRT_CFLAGS) $(JAVA_INCLUDES)
sysconf_la_LDFLAGS = -module -avoid-version $(JAVA_LIBS)
sysconf_la_LIBADD = $(RSRT_LIBS)