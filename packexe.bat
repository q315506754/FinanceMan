set "JAVA_HOME=C:\Java\jdk1.8.0_152"
echo %JAVA_HOME%
set "PATH=%PATH%;%JAVA_HOME%/bin"
echo %PATH%
cd target
::javafxpackager -deploy -native image -outdir outpackages -outfile HelloWorld -srcdir src -srcfiles target/FinanceMan.jar -appclass com.jiangli.finance.MainRunnerKt
::javapackager -deploy -native image -outdir outpackages -outfile HelloWorld -srcdir src -srcfiles target/FinanceMan.jar -appclass com.jiangli.finance.MainRunnerKt


javapackager -deploy -native image -appclass com.jiangli.finance.MainRunnerKt -srcfiles FinanceMan.jar -outdir bundle -outfile test -name testName

cd..