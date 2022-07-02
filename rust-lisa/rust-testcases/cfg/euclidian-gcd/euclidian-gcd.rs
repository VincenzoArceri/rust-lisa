fn main() {
    let mut a = 10;
    let mut b = 42;

    while b == 0 {
        let t = b;
        b = a % b;
        a = t;
    }

    let _result = a;
}

