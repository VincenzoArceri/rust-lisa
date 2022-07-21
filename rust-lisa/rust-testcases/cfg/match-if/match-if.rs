enum Color {
    RGB(u32, u32, u32),
}

fn main() {
    let color = Color::RGB(122, 17, 40);

    match color {
        Color::RGB(r, g, b) if r < 122 => println!("Red greater than 122!"),
        Color::RGB(r, g, b) => println!("No constraint on red!"),
    }
}
