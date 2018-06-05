.class public static maxmin
.super java/lang/Object




.method public static maxmin()I
.limit locals 255
.limit stack 2
invokestatic io/read()I
istore_0
iload_0
ifge if_false1
iload_0
ifge while_end2
while_init2:
iinc 0 1
iload_0
iflt while_init2
while_end2:
goto if_end1
if_false1:
iload_0
ifle while_end3
while_init3:
iinc 0 -1
iload_0
ifgt while_init3
while_end3:
if_end1:
ldc "a"
iload_0
invokestatic io/println(Ljava/lang/String;I)V
iload_0
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 2
invokestatic maxmin/maxmin()I
istore_1
ldc "a="
iload_1
invokestatic io/println(Ljava/lang/String;I)V
return
.end method
