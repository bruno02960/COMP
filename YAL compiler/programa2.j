.class public static programa2
.super java/lang/Object




.method public static f1([I)[I
.limit locals 255
.limit stack 20
iconst_0
istore_1
aload_0
arraylength
istore_2
iload_2
newarray int
astore_3
while_f1_init1:
iload_1
aload_0
arraylength
if_icmpge while_f1_end2
aload_3
iload_1
aload_0
iload_1
iaload
iastore
iinc 1 1
goto while_f1_init1
while_f1_end2:
aload_3
areturn
.end method


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
newarray int
astore_1
aload_1
iconst_0
iconst_1
iastore
aload_1
bipush 99
iconst_2
iastore
aload_1
invokestatic programa2/f1([I)[I
astore_2
aload_2
iconst_0
iaload
istore_3
aload_2
bipush 99
iaload
istore 4
ldc "first: "
iload_3
invokestatic io/println(Ljava/lang/String;I)V
ldc "last: "
iload 4
invokestatic io/println(Ljava/lang/String;I)V
bipush 100
invokestatic programa2/f2(I)[I
astore_2
aload_2
iconst_0
iaload
istore_3
aload_2
bipush 99
iaload
istore 4
ldc "first: "
iload_3
invokestatic io/println(Ljava/lang/String;I)V
ldc "last: "
iload 4
invokestatic io/println(Ljava/lang/String;I)V
return
.end method
