.class public static programa2
.super java/lang/Object




.method public static f1()[I
.limit locals 255
.limit stack 20
iconst_4
newarray int
astore_0
bipush 7
newarray int
astore_0
iconst_0
istore_1
aload_0
iconst_1
iconst_2
iastore
while_f1_init1:
iload_1
aload_0
arraylength
if_icmpeq while_f1_end2
aload_0
iload_1
iaload
istore_2
ldc "c = "
iload_2
invokestatic io/println(Ljava/lang/String;I)V
iinc 1 1
goto while_f1_init1
while_f1_end2:
aload_0
arraylength
istore_3
invokestatic io/println()V
ldc "b = "
iload_3
invokestatic io/println(Ljava/lang/String;I)V
aload_0
areturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
invokestatic programa2/f1()[I
astore_1
return
.end method
