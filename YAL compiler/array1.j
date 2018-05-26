.class public static array1
.super java/lang/Object

.method public static print_array(I)V
.limit locals 255
.limit stack 20
iload_0
newarray int
astore_1
iconst_0
istore_2
while_print_array_init1:
iload_2
iload_0
if_icmpge while_print_array_end2
aload_1
iload_2
iload_2
iastore
iinc 2 1
goto while_print_array_init1
while_print_array_end2:
iconst_0
istore_2
while_print_array_init3:
iload_2
iload_0
if_icmpge while_print_array_end4
aload_1
iload_2
iaload
istore_3
ldc "a: "
iload_3
invokestatic io/print(Ljava/lang/String;I)V
iinc 2 1
goto while_print_array_init3
while_print_array_end4:
return
.end method

.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
bipush 10
invokestatic array1/print_array(I)V
return
.end method
