.class public static maxmin
.super java/lang/Object




.method public static maxmin()I
.limit locals 255
.limit stack 20
invokestatic io/read()I
istore_0
iload_0
iflt true_maxmin1
while_maxmin_init2:
iload_0
ifle while_maxmin_end3
iinc 0 -1
goto while_maxmin_init2
while_maxmin_end3:
goto if_maxmin_end4
true_maxmin1:
while_maxmin_init5:
iload_0
ifge while_maxmin_end6
iinc 0 1
goto while_maxmin_init5
while_maxmin_end6:
if_maxmin_end4:
ldc "a"
iload_0
invokestatic io/println(Ljava/lang/String;I)V
iload_0
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
invokestatic maxmin/maxmin()I
istore_1
ldc "a="
iload_1
invokestatic io/println(Ljava/lang/String;I)V
return
.end method
