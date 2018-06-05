.class public static max
.super java/lang/Object




.method public static max(IIIII)I
.limit locals 255
.limit stack 2
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
iload_0
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 9
iconst_1
iconst_2
iconst_3
iconst_4
iconst_3
invokestatic max/max(IIIII)I
istore_1
iload_1
invokestatic io/println(I)V
iconst_1
bipush 6
iconst_3
iconst_4
iconst_5
invokestatic max/max(IIIII)I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method
