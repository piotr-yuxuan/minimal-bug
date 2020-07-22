# Python crashed when in another thread

I don't use virtual env locally.

``` python
python3 --version
Python 3.7.7
```

The actual Python code is autonomous from Clojure and produces the following result:

``` zsh
$ python3 run.py
safely when run from Clojure
ALL YOUR BASE ARE BELONG TO US
Python after
your mood = yay
```

The bug appeared when using the circuit breaker of
[safely](https://github.com/BrunoBonacci/safely), but a minimal setup
is also shown here.

``` zsh
$ lein run safely-clj-fail
OpenJDK 64-Bit Server VM warning: Options -Xverify:none and -noverify were deprecated in JDK 13 and will likely be removed in a future release.
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
before in thunk
#
# A fatal error has been detected by the Java Runtime Environment:
#
#  SIGSEGV (0xb) at pc=0x000000013c3e25a1, pid=11408, tid=28163
#
# JRE version: OpenJDK Runtime Environment AdoptOpenJDK (14.0+36) (build 14+36)
# Java VM: OpenJDK 64-Bit Server VM AdoptOpenJDK (14+36, mixed mode, sharing, tiered, compressed oops, g1 gc, bsd-amd64)
# Problematic frame:
# C  [Python+0xab5a1]  _PyEval_EvalFrameDefault+0x55c9
#
# No core dump will be written. Core dumps have been disabled. To enable core dumping, try "ulimit -c unlimited" before starting Java again
#
# An error report file with more information is saved as:
# /Users/piotr-yuxuan/src/github.com/piotr-yuxuan/minimal-bug/hs_err_pid11408.log
#
# If you would like to submit a bug report, please visit:
#   https://github.com/AdoptOpenJDK/openjdk-support/issues
# The crash happened outside the Java Virtual Machine in native code.
# See problematic frame for where to report the bug.
#

$ lein run safely-clj-no-fail
OpenJDK 64-Bit Server VM warning: Options -Xverify:none and -noverify were deprecated in JDK 13 and will likely be removed in a future release.
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
before in thunk
ALL YOUR BASE ARE BELONG TO US
Python after
after in thunk

$ lein run new-thread-fail
OpenJDK 64-Bit Server VM warning: Options -Xverify:none and -noverify were deprecated in JDK 13 and will likely be removed in a future release.
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
before in thunk
#
# A fatal error has been detected by the Java Runtime Environment:
#
#  SIGSEGV (0xb) at pc=0x00000001396d55a1, pid=11589, tid=30211
#
# JRE version: OpenJDK Runtime Environment AdoptOpenJDK (14.0+36) (build 14+36)
# Java VM: OpenJDK 64-Bit Server VM AdoptOpenJDK (14+36, mixed mode, sharing, tiered, compressed oops, g1 gc, bsd-amd64)
# Problematic frame:
# C  [Python+0xab5a1]  _PyEval_EvalFrameDefault+0x55c9
#
# No core dump will be written. Core dumps have been disabled. To enable core dumping, try "ulimit -c unlimited" before starting Java again
#
# An error report file with more information is saved as:
# /Users/piotr-yuxuan/src/github.com/piotr-yuxuan/minimal-bug/hs_err_pid11589.log
#
# If you would like to submit a bug report, please visit:
#   https://github.com/AdoptOpenJDK/openjdk-support/issues
# The crash happened outside the Java Virtual Machine in native code.
# See problematic frame for where to report the bug.
#

$ lein run new-thread-hang
OpenJDK 64-Bit Server VM warning: Options -Xverify:none and -noverify were deprecated in JDK 13 and will likely be removed in a future release.
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
before in thunk
^C%

$ lein run same-thread-no-fail
OpenJDK 64-Bit Server VM warning: Options -Xverify:none and -noverify were deprecated in JDK 13 and will likely be removed in a future release.
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
before in thunk
ALL YOUR BASE ARE BELONG TO US
Python after
after in thunk

$ lein run simple-thread-think-no-fail
OpenJDK 64-Bit Server VM warning: Options -Xverify:none and -noverify were deprecated in JDK 13 and will likely be removed in a future release.
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
before in thunk
Hello, world!
after in thunk
```

Running the same test suite from a REPL gives similar results. The
case `new-thread-hang` looks interesting in REPL because some code
looks to be actually executed.

```
=> (attempt "safely-clj-fail")
before in thunk
#
# A fatal error has been detected by the Java Runtime Environment:
#
#  SIGSEGV (0xb) at pc=0x0000000159aab5a1, pid=11868, tid=30979
#
# JRE version: OpenJDK Runtime Environment GraalVM CE 20.0.0 (11.0.6+9) (build 11.0.6+9-jvmci-20.0-b02)
# Java VM: OpenJDK 64-Bit Server VM GraalVM CE 20.0.0 (11.0.6+9-jvmci-20.0-b02, mixed mode, sharing, tiered, jvmci, jvmci compiler, compressed oops, g1 gc, bsd-amd64)
# Problematic frame:
# C  [Python+0xab5a1]  _PyEval_EvalFrameDefault+0x55c9
#
# No core dump will be written. Core dumps have been disabled. To enable core dumping, try "ulimit -c unlimited" before starting Java again
#
# An error report file with more information is saved as:
# /Users/piotr-yuxuan/src/github.com/piotr-yuxuan/minimal-bug/hs_err_pid11868.log
=> "failure-value"
Compiled method (c1)   23129 6156       3       clojure.core$fn__7324::invokeStatic (209 bytes)
 total in heap  [0x000000011b071790,0x000000011b0762c0] = 19248
 relocation     [0x000000011b0718f8,0x000000011b071b88] = 656
 main code      [0x000000011b071ba0,0x000000011b075680] = 15072
 stub code      [0x000000011b075680,0x000000011b0757e8] = 360
 oops           [0x000000011b0757e8,0x000000011b075808] = 32
 metadata       [0x000000011b075808,0x000000011b0758a0] = 152
 scopes data    [0x000000011b0758a0,0x000000011b075d90] = 1264
 scopes pcs     [0x000000011b075d90,0x000000011b0761f0] = 1120
 dependencies   [0x000000011b0761f0,0x000000011b076218] = 40
 nul chk table  [0x000000011b076218,0x000000011b0762c0] = 168
#
# If you would like to submit a bug report, please visit:
#   https://bugreport.java.com/bugreport/crash.jsp
# The crash happened outside the Java Virtual Machine in native code.
# See problematic frame for where to report the bug.
#
Disconnected from the target VM, address: '127.0.0.1:65474', transport: 'socket'

Process finished with exit code 134 (interrupted by signal 6: SIGABRT)
```

```
=> (attempt "safely-clj-no-fail")
before in thunk
ALL YOUR BASE ARE BELONG TO US
Python after
after in thunk
;; => "yay"
```

```
=> (attempt "new-thread-fail")
before in thunk
#
# A fatal error has been detected by the Java Runtime Environment:
#
#  SIGSEGV (0xb) at pc=0x0000000152cab5a1, pid=11883, tid=30979
#
# JRE version: OpenJDK Runtime Environment GraalVM CE 20.0.0 (11.0.6+9) (build 11.0.6+9-jvmci-20.0-b02)
# Java VM: OpenJDK 64-Bit Server VM GraalVM CE 20.0.0 (11.0.6+9-jvmci-20.0-b02, mixed mode, sharing, tiered, jvmci, jvmci compiler, compressed oops, g1 gc, bsd-amd64)
# Problematic frame:
# C  [Python+0xab5a1]  _PyEval_EvalFrameDefault+0x55c9
#
# No core dump will be written. Core dumps have been disabled. To enable core dumping, try "ulimit -c unlimited" before starting Java again
#
# An error report file with more information is saved as:
# /Users/piotr-yuxuan/src/github.com/piotr-yuxuan/minimal-bug/hs_err_pid11883.log
=> "timeout"
#
# If you would like to submit a bug report, please visit:
#   https://bugreport.java.com/bugreport/crash.jsp
# The crash happened outside the Java Virtual Machine in native code.
# See problematic frame for where to report the bug.
#
Disconnected from the target VM, address: '127.0.0.1:65482', transport: 'socket'

Process finished with exit code 134 (interrupted by signal 6: SIGABRT)
```

```
=> (attempt "new-thread-hang")
before in thunk
Evaluation interrupted.
Jul 22, 2020 11:43:03 PM com.sun.jna.Native$1 uncaughtException
WARNING: JNA: Callback libpython_clj.python.object$make_tuple_fn$reify__30135@589cbc4 threw the following exception
java.lang.ThreadDeath
	at java.base/java.lang.Thread.stop(Thread.java:942)
	at nrepl.middleware.session$session_exec$fn__36375.invoke(session.clj:190)
	at nrepl.middleware.session$interrupt_session.invokeStatic(session.clj:210)
	at nrepl.middleware.session$interrupt_session.invoke(session.clj:207)
	at nrepl.middleware.session$session$fn__36397.invoke(session.clj:268)
	at nrepl.middleware$wrap_conj_descriptor$fn__35952.invoke(middleware.clj:16)
	at nrepl.server$handle_STAR_.invokeStatic(server.clj:18)
	at nrepl.server$handle_STAR_.invoke(server.clj:15)
	at nrepl.server$handle$fn__36434.invoke(server.clj:27)
	at clojure.core$binding_conveyor_fn$fn__5754.invoke(core.clj:2030)
	at clojure.lang.AFn.call(AFn.java:18)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:834)

ALL YOUR BASE ARE BELONG TO US
Python after
after in thunk
Execution error at libpython-clj.python.interpreter/check-error-throw (interpreter.clj:499).
Traceback (most recent call last):
  File "/Users/piotr-yuxuan/src/github.com/piotr-yuxuan/minimal-bug/src/python/MinimalBug.py", line 12, in call_me_baby
    lambda: some_wrapped_python(1, 2, 3, first=1, second=2, third=3))()
  File "/Users/piotr-yuxuan/src/github.com/piotr-yuxuan/minimal-bug/src/python/ClojureBridge/ClojureBridge.py", line 18, in inner
    return safely_clj(thunk)
SystemError: <built-in function unnamed_function> returned NULL without setting an error
```

```
(attempt "same-thread-no-fail")
before in thunk
ALL YOUR BASE ARE BELONG TO US
Python after
after in thunk
=> "yay"
```

```
(attempt "simple-thread-think-no-fail")
before in thunk
Hello, world!
after in thunk
=> nil
```
