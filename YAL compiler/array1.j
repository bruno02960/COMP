.class public static array1
.super java/lang/Object




.method public static print_array(I)V
.limit locals 255
.limit stack 20
iload_0
newarray int
astore_1
iconst_0
astore_2
while_print_array_init2:
iload_0
iload_2
if_icmplt while_print_array_end3
goto while_print_array_end3
iload_2
aload_1
iload_2
iastore
iload_2
iconst_1
isub
istore_2
goto while_print_array_init2
while_print_array_end3:
iconst_0
astore_2
while_print_array_init4:
iload_0
iload_2
if_icmplt while_print_array_end5
goto while_print_array_end5
iload_1
iload_2
astore_3
ldc "a: "
iload_3
invokestatic io/print(Ljava/lang/String;I)VI

pop
iload_2
iconst_1
imul
istore_2
goto while_print_array_init4
while_print_array_end5:
return
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
ldc 10
invokestatic array1/print_array(I)V
return
.end method
