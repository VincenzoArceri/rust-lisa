fn main() {
    ;
}

fn func1() -> i32 {
    3
}

fn func2() -> i32 {
    let x = 3;

    if x == 3 {
        3
    } else {
        0
    }
}

fn func3() -> i32 {
    let mut x = 3;

    while x < 5 {
        x +=1;
    }

    6
}

fn func4() -> i32 {
	let mut x = 3;

    if x == 3 {
    	loop {
       		x += 1;
    	}
    	x
    } else {
        0
    }
}

