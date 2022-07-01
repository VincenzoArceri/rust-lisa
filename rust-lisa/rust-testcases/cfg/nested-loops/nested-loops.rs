fn main() {
    let mut a = 0;
    let b = 7;

    loop {
        for _i in 0..b {
            let mut c = a;

            while c < b {
                c += 1;
            }

            a += 1;
        }
    }
}

