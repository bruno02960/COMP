.class public static aval6
.super java/lang/Object




.method public static sqrt(I)I
.limit locals 255
.limit stack 20
iload_0
istore_1
iconst_0
istore_2
iconst_0
istore_3
iconst_0
istore 4
iconst_0
istore 5
while_sqrt_init1:
iload 5
bipush 6
if_icmpge while_sqrt_end2
iload_2
iload_3
iadd
istore 6
iload 6
iconst_2
ishl
istore 7
iload 7
iconst_1
ior
istore 8
iload_3
iconst_1
ishl
istore 9
iload 4
iconst_2
ishl
istore 10
iload_1
bipush 10
ishr
istore 11
iload 11
iconst_3
iand
istore 12
iload 10
iload 12
ior
istore 4
iload_1
iconst_2
ishl
istore_1
iload 8
iload 4
if_icmple true_sqrt3
iload 9
istore_3
iload_2
iconst_2
ishl
istore_2
goto if_sqrt_end4
true_sqrt3:
iload 9
iconst_1
ior
istore_3
iload 8
istore_2
if_sqrt_end4:
iinc 5 1
goto while_sqrt_init1
while_sqrt_end2:
iload_3
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
bipush 17
invokestatic aval6/sqrt(I)I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method
