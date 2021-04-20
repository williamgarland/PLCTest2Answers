function outer() {
    var x = 10;
    console.log("outer: x = " + x);
    function f1() {
        var y = 20;
        var x = -10;
        console.log("f1: x = " + x + ", y = " + y);
        function f2() {
            var z = 30;
            var y = -20;
            console.log("f2: x = " + x + ", y = " + y + ", z = " + z);
            function f3() {
                var w = 40;
                var z = -30;
                console.log("f3: x = " + x + ", y = " + y + ", z = " + z + ", w = " + w);
            }
            f3();
        }
        f2();
    }
    f1();
}

outer();