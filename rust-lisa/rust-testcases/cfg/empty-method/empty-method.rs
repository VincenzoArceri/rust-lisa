struct Empty {
}

impl Empty {
	pub fn empty() {
	}
	
	pub fn new() -> Empty {
	    Empty{}
	}
	
	pub fn empty_self(&self) {
	}
}

fn main() {
	Empty::empty();
	
	let e = Empty::new();
	e.empty_self();
}
