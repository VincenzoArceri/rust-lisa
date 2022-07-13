struct Rect {
    width : usize,
    height : usize,
}

impl Rect {
    pub fn area(&self) -> usize {
        self.width * self.height
    }

    pub fn perim(&self) -> usize {
        2*self.width + 2*self.height
    }

    pub fn new(width : usize, height : usize) -> Rect {
        Rect {
            width,
            height,
        }
    }
}

fn main() {
    let r = Rect::new(10, 5);
    let _a = r.area();
    let _b = r.perim();
}
