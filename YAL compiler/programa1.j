.class public static programa1
.super java/lang/Object


.field public static data [I
.field public static mx I = 0
.field public static mn I = 0


.method public static det([I)V
.limit locals 255
.limit stack 4
iconst_0
istore 4
aload_0
arraylength
iconst_1
isub
istore_1
iconst_0
iload_1
if_icmpge while_end1
while_init1:
aload_0
iload 4
iaload
istore_2
iinc 4 1
aload_0
iload 4
iaload
istore_1
iload_2
iload_1
invokestatic library1/max(II)I
putstatic programa1/mx I
iload_2
iload_1
invokestatic library1/min(II)I
putstatic programa1/mn I
iload 4
iload_1
if_icmplt while_init1
while_end1:
return
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 3
getstatic programa1/data [I
invokestatic programa1/det([I)V
ldc "max: "
getstatic programa1/mx I
invokestatic io/println(Ljava/lang/String;I)V
ldc "min: "
getstatic programa1/mn I
invokestatic io/println(Ljava/lang/String;I)V
return
.end method
.method public static <clinit>()V 

.limit stack 1

bipush 100
newarray int
putstatic programa1/data [I
return 

.end method

