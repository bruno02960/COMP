.class public static max_array
.super java/lang/Object




.method public static maxarray([I)I
.limit locals 255
.limit stack 3
aload_0
iconst_0
iaload
istore_2
iconst_1
istore_1
iconst_1
aload_0
arraylength
if_icmpge while_end1
while_init1:
iload_2
aload_0
iload_1
iaload
if_icmpge if_end2
aload_0
iload_1
iaload
istore_2
if_end2:
iinc 1 1
iload_1
aload_0
arraylength
if_icmplt while_init1
while_end1:
ldc "max: "
iload_2
invokestatic io/print(Ljava/lang/String;I)V
iload_2
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 3
bipush 10
newarray int
astore_1
iconst_0
istore_0
iconst_0
bipush 10
if_icmpge while_end3
while_init3:
aload_1
iload_0
iload_0
iastore
iinc 0 1
iload_0
bipush 10
if_icmplt while_init3
while_end3:
aload_1
invokestatic max_array/maxarray([I)I
istore_1
return
.end method
