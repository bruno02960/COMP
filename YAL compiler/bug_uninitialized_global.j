.class public static bug_uninitialized_global
.super java/lang/Object


.field public static c [I


.method public static main([Ljava/lang/String;)V
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

