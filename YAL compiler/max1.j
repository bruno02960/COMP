.class public static max1
.super java/lang/Object




.method public static max()I
.limit locals 255
.limit stack 2
invokestatic io/read()I
istore_0
invokestatic io/read()I
istore_1
invokestatic io/read()I
istore_2
invokestatic io/read()I
istore_3
invokestatic io/read()I
istore 4
iload_0
istore_0
iload_0
iload_1
if_icmpge if_end1
iload_1
istore_0
if_end1:
iload_0
iload_2
if_icmpge if_end2
iload_2
istore_0
if_end2:
iload_0
iload_3
if_icmpge if_end3
iload_3
istore_0
if_end3:
iload_0
iload 4
if_icmpge if_end4
iload 4
istore_0
if_end4:
ldc "max "
iload_0
invokestatic io/print(Ljava/lang/String;I)V
iload_0
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 1
invokestatic max1/max()I
istore_1
return
.end method
