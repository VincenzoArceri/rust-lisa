fn main() {
	;
}

fn func0() {
	let _x = 2;
}

fn func1() {
	let _x = 6;
	return;
}

fn func3() {
	let _x = 4;

	if _x == 4 {
		let _y = _x + 2;
		let _z = 6;
		return;
	}

	let _y = 6;
	return;
}

fn func4() {
	let _x = 4;

	if _x == 4 {
		let _y = _x + 2;
		let _z = 6;
		return;
	}
	return;
}

fn func5() {
	let _x = 4;

	loop {
		let _x = 6;
	}
	return;
}

fn func6() {
	let _x = 4;

	loop {
		let _x = 6;
		return;
	}
	return;
}

fn func7() {
    let _x = 1;
    if _x == 2 {
        if _x < 3 {
            return;
        }
    }
}

