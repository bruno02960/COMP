.class public static programa3
.super java/lang/Object




.method public static f1([I)[I
.limit locals 255
.limit stack 4
iconst_0
istore_1
aload_0
arraylength
newarray int
astore_0
iconst_0
aload_0
arraylength
if_icmpge while_end1
while_init1:
aload_0
iload_1
aload_0
iload_1
iaload
iastore
iinc 1 1
iload_1
aload_0
arraylength
if_icmplt while_init1
while_end1:
aload_0
areturn
.end method


.method public static f2(I)[I
.limit locals 255
.limit stack 4
iload_0
newarray int
astore_0
aload_0
arraylength
init:
iconst_1
isub
dup
dup
iflt end
aload_0
swap
iconst_1
iastore
goto init
end:
iconst_1
areturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 5
bipush 100
newarray int
astore_0
aload_0
iconst_0
iconst_1
iastore
aload_0
bipush 99
iconst_2
iastore
aload_0
invokestatic programa3/f1([I)[I
astore_1
aload_1
iconst_0
iaload
istore_1
aload_1
bipush 99
iaload
istore_0
ldc "first: "
iload_1
invokestatic io/println(Ljava/lang/String;I)V
ldc "last: "
iload_0
invokestatic io/println(Ljava/lang/String;I)V
bipush 100
invokestatic programa3/f2(I)[I
astore_1
aload_1
iconst_0
iaload
istore_1
aload_1
bipush 99
iaload
istore_0
ldc "first: "
iload_1
invokestatic io/println(Ljava/lang/String;I)V
ldc "last: "
iload_0
invokestatic io/println(Ljava/lang/String;I)V
return
.end method
