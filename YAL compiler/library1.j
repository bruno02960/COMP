.class public static library1
.super java/lang/Object




.method public static max(II)I
.limit locals 255
.limit stack 2
iload_0
iload_1
if_icmple if_false1
iload_0
istore_0
goto if_end1
if_false1:
iload_1
istore_0
if_end1:
iload_0
ireturn
.end method


.method public static min(II)I
.limit locals 255
.limit stack 2
iload_0
iload_1
if_icmple if_false2
iload_1
istore_0
goto if_end2
if_false2:
iload_0
istore_0
if_end2:
iload_0
ireturn
.end method
