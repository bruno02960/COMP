.class public static bug_uninitialized_global
.super java/lang/Object


.field public static b I = 10
.field public static c [I


.method public static f(I)I
.limit locals 255
.limit stack 20
getstatic bug_uninitialized_global/c [I
arraylength
init:
iconst_1
isub
dup
dup
iflt end
getstatic bug_uninitialized_global/c [I
swap
getstatic bug_uninitialized_global/c [I
arraylength
iastore
goto init
end:
while_f_init1:
iload_0
getstatic bug_uninitialized_global/b I
if_icmpge while_f_end2
iinc 0 1
goto while_f_init1
while_f_end2:
iload_0
getstatic bug_uninitialized_global/b I
if_icmple true_f3
goto if_f_end4
true_f3:
bipush 69
istore_0
if_f_end4:
getstatic bug_uninitialized_global/c [I
iconst_2
iaload
istore_1
iload_1
invokestatic io/println(I)V
getstatic bug_uninitialized_global/c [I
iconst_2
iconst_5
iastore
getstatic bug_uninitialized_global/c [I
iconst_2
iaload
istore_1
iload_1
invokestatic io/println(I)V
iload_1
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
iconst_0
invokestatic bug_uninitialized_global/f(I)I

pop
return
.end method
.method static public <clinit>()V 

.limit stack 255

.limit locals 255

bipush 95
newarray int
putstatic bug_uninitialized_global/c [I
return 

.end method

