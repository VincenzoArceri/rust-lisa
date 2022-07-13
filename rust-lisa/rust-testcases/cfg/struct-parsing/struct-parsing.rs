struct Number {
    num: isize,
}

impl Number {
	pub fn zero() {
        let _x = 2;
    }
}

fn main() {
	let _x = 2;
	let num = 3;
    let _y = Number::zero();

	let a = Number {
		num,
	};

    let _b = a.num;
}
