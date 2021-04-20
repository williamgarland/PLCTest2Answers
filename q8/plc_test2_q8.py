def outer():
    x = 10
    print("outer: x = " + str(x))
    def f1():
        y = 20
        x = -10
        print("f1: x = " + str(x) + ", y = " + str(y))
        def f2():
            z = 30
            y = -20
            print("f2: x = " + str(x) + ", y = " + str(y) + ", z = " + str(z))
            def f3():
                w = 40
                z = -30
                print("f3: x = " + str(x) + ", y = " + str(y) + ", z = " + str(z) + ", w = " + str(w))
            f3()
        f2()
    f1()

outer()