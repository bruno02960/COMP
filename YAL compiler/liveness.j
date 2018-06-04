.class public static liveness
.super java/lang/Object


.field public static g1 I = 2
.field public static g2 I = 3
.field public static g3 I = 4


.method public static f1(II)I
.limit locals 255
.limit stack 2
iconst_2
istore_1
iload_0
istore_1
iconst_2
iload_0
iadd
istore_1
iconst_2
iload_1
iadd
istore_1
iconst_2
istore_0
getstatic liveness/g2 I
putstatic liveness/g1 I
getstatic liveness/g3 I
istore_0
getstatic liveness/g1 I
getstatic liveness/g2 I
iadd
istore_0
iconst_2
invokestatic liveness/f2(I)V
iload_1
iconst_2
invokestatic liveness/f3(II)I
istore_0
iconst_2
istore_0
iconst_2
ireturn
.end method


.method public static f2(I)V
.limit locals 255
.limit stack 2
iconst_1
iconst_2
iadd
istore_0
return
.end method


.method public static f3(II)I
.limit locals 255
.limit stack 2
iconst_0
istore_1
iload_0
iload_1
if_icmple if_false1
iload_0
istore_1
goto if_end1
if_false1:
iload_1
istore_1
if_end1:
iconst_0
istore_0
iconst_0
bipush 10
if_icmpge while_end2
while_init2:
iinc 1 1
iconst_0
bipush 10
if_icmplt while_init2
while_end2:
iconst_0
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 5
iconst_1
iconst_2
invokestatic liveness/f1(II)I
istore_1
iload_1
iconst_3
invokestatic liveness/f3(II)I
istore_2
ldc "a = "
iload_1
invokestatic io/println(Ljava/lang/String;I)V
ldc "b = "
iload_2
invokestatic io/println(Ljava/lang/String;I)V
return
.end method
