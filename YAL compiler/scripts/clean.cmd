setlocal
cd ..
del *.j
for %i in (*.class) do if not %i == io.class del %i