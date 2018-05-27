.class public static programa2
.super java/lang/Object




.method public static f2(I)[I
.limit locals 255
.limit stack 20
iload_0
newarray int
astore_1
aload_1
arraylength
init:
iconst_1
isub
dup
iflt end
aload_1
swap
iconst_1
iastore
goto init
end:
aload_1
areturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
bipush 100
invokestatic programa2/f2(I)[I
istore_1
return
.end method
