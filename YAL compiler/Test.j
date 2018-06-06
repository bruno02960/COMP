.class public static Test
.super java/lang/Object




.method public static f1()V
.limit locals 0
.limit stack 0
return
.end method


.method public static f2(II)I
.limit locals 3
.limit stack 2
iconst_0
istore_2
iload_0
iload_1
if_icmple if_false1
iconst_1
istore_2
goto if_end1
if_false1:
iconst_2
istore_2
if_end1:
iload_2
ireturn
.end method


.method public static f3(II)I
.limit locals 2
.limit stack 3
iconst_0
istore_1
iload_0
iload_1
iload_1
iadd
if_icmpne if_false2
iconst_1
istore_1
goto if_end2
if_false2:
iconst_2
istore_1
if_end2:
iload_1
ireturn
.end method


.method public static f4(II)V
.limit locals 2
.limit stack 3
iload_0
iload_1
if_icmple while_end3
while_init3:
ldc "a = "
iload_0
invokestatic io/println(Ljava/lang/String;I)V
iinc 0 -1
iload_0
iload_1
if_icmpgt while_init3
while_end3:
return
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 1
.limit stack 6
invokestatic Test/f1()V
iconst_5
iconst_0
invokestatic Test/f2(II)I
istore_0
ldc "x = "
iload_0
invokestatic io/println(Ljava/lang/String;I)V
iconst_2
iconst_1
invokestatic Test/f3(II)I
istore_0
ldc "x = "
iload_0
invokestatic io/println(Ljava/lang/String;I)V
iconst_5
iconst_1
invokestatic Test/f4(II)V
return
.end method
