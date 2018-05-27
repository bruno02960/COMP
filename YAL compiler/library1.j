.class public static library1
.super java/lang/Object

.method public static max(II)I
.limit locals 255
.limit stack 20
iload_0
iload_1
if_icmpgt true_max1
iload_1
istore_2
goto if_max_end2
true_max1:
iload_0
istore_2
if_max_end2:
iload_2
ireturn
.end method


.method public static min(II)I
.limit locals 255
.limit stack 20
iload_0
iload_1
if_icmpgt true_min3
iload_0
istore_2
goto if_min_end4
true_min3:
iload_1
istore_2
if_min_end4:
iload_2
ireturn
.end method
