.class public static max_array
.super java/lang/Object

.method public static maxarray([I)I
.limit locals 255
.limit stack 20
aload_0
iconst_0
iaload
istore_1
iconst_1
istore_2
while_maxarray_init1:
iload_2
aload_0
arraylength
if_icmpge while_maxarray_end2
iload_1
iload_0
if_icmplt true_maxarray3
goto if_maxarray_end4
true_maxarray3:
aload_0
iload_2
iaload
istore_1
if_maxarray_end4:
iinc 2 1
goto while_maxarray_init1
while_maxarray_end2:
ldc "max: "
iload_1
invokestatic io/print(Ljava/lang/String;I)V
iload_1
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
bipush 10
newarray int
astore_1
iconst_0
istore_2
while_main_init5:
iload_2
bipush 10
if_icmpge while_main_end6
aload_1
iload_2
iload_2
iastore
iinc 2 1
goto while_main_init5
while_main_end6:
aload_1
invokestatic max_array/maxarray([I)I
istore_3
return
.end method
