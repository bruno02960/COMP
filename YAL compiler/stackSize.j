.class public static stackSize
.super java/lang/Object




.method public static f(I)V
.limit locals 255
.limit stack 20
iload_0
iload_0
iload_0
iload_0
iload_0
invokestatic stackSize/h(IIII)I
if_icmpgt true_f1
ldc "Not greater"
invokestatic io/println(Ljava/lang/String;)V
goto if_f_end2
true_f1:
ldc "Greater"
invokestatic io/println(Ljava/lang/String;)V
if_f_end2:
return
.end method


.method public static g(I)I
.limit locals 255
.limit stack 20
iload_0
iload_0
iload_0
iload_0
iload_0
invokestatic stackSize/h(IIII)I
imul
istore_1
iload_1
ireturn
.end method


.method public static h(IIII)I
.limit locals 255
.limit stack 20
iload_0
iload_1
iadd
istore 4
iload 4
iload_2
iadd
istore 4
iload 4
iload_3
iadd
istore 4
iload 4
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
iconst_2
istore_1
iload_1
invokestatic stackSize/f(I)V
return
.end method
