.class public static nestedBranch
.super java/lang/Object




.method public static sign(I)I
.limit locals 255
.limit stack 1
iload_0
ifge if_false1
iconst_m1
istore_0
goto if_end1
if_false1:
iload_0
ifne if_false2
iconst_0
istore_0
goto if_end2
if_false2:
iconst_1
istore_0
if_end2:
if_end1:
iload_0
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 2
bipush -10
istore_1
bipush 10
istore_3
bipush -10
bipush 10
iadd
istore_1
bipush -10
invokestatic nestedBranch/sign(I)I
istore_2
iconst_0
invokestatic nestedBranch/sign(I)I
istore_3
bipush 10
invokestatic nestedBranch/sign(I)I
istore 4
iload_2
invokestatic io/println(I)V
iload_3
invokestatic io/println(I)V
iload 4
invokestatic io/println(I)V
return
.end method
