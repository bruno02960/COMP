.class public static aval8
.super java/lang/Object




.method public static max1()I
.limit locals 255
.limit stack 3
invokestatic io/read()I
istore_0
invokestatic io/read()I
istore_1
iload_1
istore_0
iload_0
iload_1
if_icmple if_end1
iload_0
istore_0
if_end1:
iconst_2
iconst_4
imul
istore_2
ldc "a"
iload_0
invokestatic io/print(Ljava/lang/String;I)V
iload_0
bipush -23
if_icmpge if_false2
iconst_0
istore_0
goto if_end2
if_false2:
bipush -2
iconst_4
imul
istore_0
if_end2:
iload_0
istore_0
iload_0
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 1
invokestatic aval8/max1()I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method
