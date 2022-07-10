struct Test {
	field1 : i32,
}

fn main() {
	let _x : i32 = 4;
	
	let _y1 : _ = 7;
	let _y2 = 7;
	
	let _z : [i32; 4] = [1, 2, 3, 4];
	
	let mut _a : (i32, char, f64) = (42, 'A', 3.3);
	
	let _ptr_mut : *mut i32 = &42; 
	let _ptr_const : *const i32 = &42; 
	
	let _s = Test {
		field1 : 42,
	};
}
