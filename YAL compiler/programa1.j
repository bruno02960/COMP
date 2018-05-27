.class public static programa1
.super java/lang/Object


.field public static data [I
.field public static mx I = 0
.field public static mn I = 0


.method public static det([I)V
.limit locals 255
.limit stack 20
iconst_0
istore_1
aload_0
arraylength
iconst_1
isub
istore_2
while_det_init1:
iload_1
iload_2
if_icmpge while_det_end2
aload_0
iload_1
iaload
istore_3
iinc 1 1
aload_0
iload_1
iaload
istore 4
iload_3
iload 4
invokestatic library1/max(II)I
putstatic programa1/mx I
iload_3
iload 4
invokestatic library1/min(II)I
putstatic programa1/mn I
goto while_det_init1
while_det_end2:
return
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
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
.method static public <clinit>()V 

.limit stack 255

.limit locals 255

bipush 100
newarray int
putstatic programa1/data [I
return 

.end method

