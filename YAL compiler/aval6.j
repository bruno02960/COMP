.class public static aval6
.super java/lang/Object




.method public static sqrt(I)I
.limit locals 255
.limit stack 2
iload_0
istore 6
iconst_0
istore 5
iconst_0
istore_2
iconst_0
istore_1
iconst_0
istore 4
iconst_0
bipush 6
if_icmpge while_end1
while_init1:
iload 5
iload_2
iadd
istore_1
iload_1
iconst_2
ishl
istore_2
iload_2
iconst_1
ior
istore_3
iload_2
iconst_1
ishl
istore 4
iload_1
iconst_2
ishl
istore 5
iload 6
bipush 10
ishr
istore 6
iload 6
iconst_3
iand
istore 7
iload 5
iload 7
ior
istore_1
iload 6
iconst_2
ishl
istore 6
iload_3
iload_1
if_icmpgt if_false2
iload 4
iconst_1
ior
istore_2
iload_3
istore 5
goto if_end2
if_false2:
iload 4
istore_2
iload 5
iconst_2
ishl
istore 5
if_end2:
iinc 4 1
iload 4
bipush 6
if_icmplt while_init1
while_end1:
iload_2
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 1
bipush 17
invokestatic aval6/sqrt(I)I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method
