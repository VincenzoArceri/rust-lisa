fn main() {
	;
}

fn func1() -> i32 {
	return 3;
}

fn func2() -> i32 {
	let x = 2;
	if x == 2 {
		return x;
	}
	
	return 5;
}

fn func3() -> i32 {
	let x = 2;
	if x == 2 {
		if x == 3 {
			return x;
		} else {
			return x + 1;
		}
	}
	
	return 5;
}

fn func4() -> i32 {
	let mut x = 1;
	
	loop {
		x += 1;
	}
	
	return x;
}

fn func5() -> i32 {
	let mut x = 5;
	
	loop {
		if x == 4 {
			x += 1;
		}
	}
	
	return x;
}
