struct Test {
	field1 : i32,
}

fn main() {
	let x : i32 = 4;
	
	let y1 : _ = 7;
	let y2 = 7;
	
	let z : [i32; 4] = [1, 2, 3, 4];
	
	let mut a : (i32, char, f64) = (42, 'A', 3.3);
	
	let ptr: *const i32 = &42; 
	
	let s = Test {
		field1 : 42,
	};
}
