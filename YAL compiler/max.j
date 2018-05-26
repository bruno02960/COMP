.class public static max
.super java/lang/Object

.method public static max(IIIII)I
.limit locals 255
.limit stack 20
iload_0
istore 5
iload 5
iload_1
if_icmplt true_max1
goto if_max_end2
true_max1:
iload_1
istore 5
if_max_end2:
iload 5
iload_2
if_icmplt true_max3
goto if_max_end4
true_max3:
iload_2
istore 5
if_max_end4:
iload 5
iload_3
if_icmplt true_max5
goto if_max_end6
true_max5:
iload_3
istore 5
if_max_end6:
iload 5
iload 4
if_icmplt true_max7
goto if_max_end8
true_max7:
iload 4
istore 5
if_max_end8:
iload 5
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
iconst_1
iconst_2
iconst_3
iconst_4
iconst_3
invokestatic max(IIIII)I
istore_1
iload_1
invokestatic io/println(I)V
iconst_1
bipush 6
iconst_3
iconst_4
iconst_5
invokestatic max(IIIII)I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method
