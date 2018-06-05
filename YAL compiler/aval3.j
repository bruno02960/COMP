.class public static aval3
.super java/lang/Object




.method public static f(II)I
.limit locals 255
.limit stack 2
iload_0
iload_1
if_icmplt if_false1
iconst_2
istore_0
goto if_end1
if_false1:
iconst_4
istore_0
if_end1:
iload_0
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 3
iconst_2
istore_1
iconst_3
istore_0
iload_1
iconst_3
invokestatic aval3/f(II)I
istore_1
iload_1
invokestatic io/println(I)V
bipush 6
istore_1
iload_1
iconst_3
invokestatic aval3/f(II)I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method
