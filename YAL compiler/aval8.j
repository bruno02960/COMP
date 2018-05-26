.class public static aval8
.super java/lang/Object

.method public static max1()I
.limit locals 255
.limit stack 20
invokestatic io/read()I
istore_0
invokestatic io/read()I
istore_1
iload_1
istore_2
iload_0
iload_1
if_icmpgt true_max11
goto if_max1_end2
true_max11:
iload_0
istore_2
if_max1_end2:
iconst_2
iconst_4
imul
istore_3
ldc "a"
iload_0
invokestatic io/print(Ljava/lang/String;I)V
iload_0
bipush -23
if_icmplt true_max13
bipush -2
iconst_4
imul
istore 4
goto if_max1_end4
true_max13:
iconst_0
istore 4
if_max1_end4:
iload 4
istore_2
iload_2
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
invokestatic aval8/max1()I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method
