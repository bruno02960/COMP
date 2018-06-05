.class public static aval5
.super java/lang/Object




.method public static f(II)I
.limit locals 255
.limit stack 2
bipush 10
istore_2
iload_0
iload_1
if_icmpne if_false1
iload_0
bipush 10
if_icmpge while_end2
while_init2:
iinc 0 1
iload_0
iload_2
if_icmplt while_init2
while_end2:
iload_0
iconst_2
ishl
istore_1
goto if_end1
if_false1:
iload_1
iload_0
iadd
istore_1
if_end1:
iload_1
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 3
iconst_4
iconst_5
invokestatic aval5/f(II)I
istore_1
iload_1
invokestatic io/println(I)V
iconst_2
iconst_2
invokestatic aval5/f(II)I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method
