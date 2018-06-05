.class public static array1
.super java/lang/Object




.method public static print_array(I)V
.limit locals 255
.limit stack 3
iload_0
newarray int
astore_3
iconst_0
istore_2
iconst_0
iload_0
if_icmpge while_end1
while_init1:
aload_3
iload_2
iload_2
iastore
iinc 2 1
iload_2
iload_0
if_icmplt while_init1
while_end1:
iconst_0
istore_2
iconst_0
iload_0
if_icmpge while_end2
while_init2:
aload_3
iload_2
iaload
istore_1
ldc "a: "
iload_1
invokestatic io/print(Ljava/lang/String;I)V
iinc 2 1
iload_2
iload_0
if_icmplt while_init2
while_end2:
return
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 1
bipush 10
invokestatic array1/print_array(I)V
return
.end method
