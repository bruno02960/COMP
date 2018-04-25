:: compile Jasmin file into bytecodes and then run it

cls
setlocal
cd ..
java -jar jasmin.jar %1.j
java -cp . %1